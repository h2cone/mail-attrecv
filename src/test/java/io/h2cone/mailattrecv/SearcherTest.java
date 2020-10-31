/*
 * Copyright 2020 huangh https://github.com/h2cone
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.h2cone.mailattrecv;

import io.h2cone.mailattrecv.search.MailFilter;
import io.h2cone.mailattrecv.search.MailQuery;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearcherTest extends Tester {
    private static final Logger log = LoggerFactory.getLogger(Tester.class);

    private Message lastMessage() throws IOException, MessagingException {
        Folder inbox = openInbox();
        Message lastMessage = inbox.getMessage(inbox.getMessageCount());
        Assert.assertNotNull(lastMessage);
        log.warn("lastMessage: {}", format(lastMessage));
        return lastMessage;
    }

    @Test
    public void matchSubject() throws IOException, MessagingException {
        Message message = lastMessage();
        MailQuery query = new MailQuery()
                .setFrom("")
                .setSubject(message.getSubject());
        MailFilter filter = new MailFilter(query);
        Assert.assertTrue(filter.match(message));
    }

    @Test
    public void matchFrom() throws IOException, MessagingException {
        Message message = lastMessage();
        Address[] addressArray = message.getFrom();
        Assert.assertNotNull(addressArray);
        Assert.assertTrue(addressArray.length > 0);
        List<String> fromList = Arrays.stream(addressArray).map(address -> {
            String from = address.toString();
            try {
                return MimeUtility.decodeText(from);
            } catch (UnsupportedEncodingException e) {
                log.error("failed to decode text: " + from, e);
            }
            return from;
        }).collect(Collectors.toList());
        log.warn("fromList: {}", fromList);
        String from = fromList.get(0);
        Assert.assertNotNull(from);
        Assert.assertFalse(from.isEmpty());
        from = from.substring(from.indexOf("<") + 1, from.lastIndexOf(">"));
        log.warn("from: {}", from);

        MailQuery query = new MailQuery()
                .setFrom(from)
                .setSubject("");
        MailFilter filter = new MailFilter(query);
        Assert.assertTrue(filter.match(message));
    }
}
