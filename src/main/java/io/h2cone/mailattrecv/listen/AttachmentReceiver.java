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

package io.h2cone.mailattrecv.listen;

import io.h2cone.mailattrecv.download.AttachmentDownloader;
import io.h2cone.mailattrecv.search.MailFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author h
 */
public class AttachmentReceiver implements MessageCountListener {
    private static final Logger log = LoggerFactory.getLogger(AttachmentReceiver.class);

    private final MailFilter searchTerm;
    private final AttachmentDownloader downloader;

    public AttachmentReceiver(MailFilter searchTerm, AttachmentDownloader downloader) {
        this.searchTerm = searchTerm;
        this.downloader = downloader;
    }

    @Override
    public void messagesAdded(MessageCountEvent event) {
        Message[] messages = event.getMessages();
        for (Message message : messages) {
            if (searchTerm.match(message)) {
                int messageNumber = message.getMessageNumber();
                try {
                    List<String> files = downloader.download(messageNumber);
                    if (Objects.nonNull(files) && !files.isEmpty()) {
                        log.info("download completed, total: {}, files: {}", files.size(), files);
                    }
                } catch (MessagingException | IOException e) {
                    log.error(String.format("failed to download attachments, messageNumber: %d", messageNumber), e);
                }
            }
        }
    }

    @Override
    public void messagesRemoved(MessageCountEvent e) {
    }
}
