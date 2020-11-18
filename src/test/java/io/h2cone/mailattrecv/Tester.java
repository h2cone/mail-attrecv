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

import io.h2cone.mailattrecv.client.ImapClient;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Tester {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected static final String PROPERTIES_APP = "app.properties";

    protected Folder openInbox() throws MessagingException, IOException {
        Properties props = loadProps();
        ImapClient client = new ImapClient(props);
        Folder inbox = null;
        try {
            inbox = client.getInbox();
        } catch (Exception e) {
            thrown.expect(AuthenticationFailedException.class);
            throw e;
        }
        Assert.assertNotNull(inbox);
        if (!inbox.isOpen()) {
            inbox.open(Folder.READ_ONLY);
        }
        return inbox;
    }

    protected Properties loadProps() throws IOException {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(PROPERTIES_APP)) {
            Assert.assertNotNull(in);
            try (InputStreamReader reader = new InputStreamReader(in)) {
                props.load(reader);
            }
        }
        return props;
    }

    public String format(Message msg) throws MessagingException, UnsupportedEncodingException {
        String format = "%d｜%s｜%s";
        Address[] addresses = msg.getFrom();
        return String.format(format,
                msg.getMessageNumber(),
                MimeUtility.decodeText(addresses[0].toString()),
                msg.getSubject());
    }
}
