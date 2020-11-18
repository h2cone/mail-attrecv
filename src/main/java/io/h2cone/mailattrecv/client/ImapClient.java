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

package io.h2cone.mailattrecv.client;

import com.sun.mail.imap.IMAPFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author h
 */
public class ImapClient {
    private static final Logger log = LoggerFactory.getLogger(ImapClient.class);

    private final Properties props;

    public ImapClient(Properties props) {
        this.props = props;
    }

    public Folder getInbox() throws MessagingException {
        Session session = Session.getDefaultInstance(props);
        Folder inbox;
        String username = props.getProperty("mail.protocol.username");
        if (Objects.isNull(username) || username.isEmpty()) {
            username = System.getenv("mail_protocol_username");
        }
        String password = props.getProperty("mail.protocol.password");
        if (Objects.isNull(password) || password.isEmpty()) {
            password = System.getenv("mail_protocol_password");
        }
        try (Store store = session.getStore("imap")) {
            store.connect(username, password);
            inbox = store.getFolder("INBOX");
        }
        return inbox;
    }

    @Deprecated
    public void testIdle(Folder folder) {
        int initialDelay = getTestIdleInitialDelay();
        int period = getTestIdlePeriod();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if (!idleSupported(folder)) {
                try {
                    folder.getMessageCount();
                } catch (MessagingException e) {
                    log.error("failed to get message count", e);
                }
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    private int getTestIdlePeriod() {
        String period = props.getProperty("mail.idle.test.period");
        return Objects.isNull(period) || period.isEmpty() ? 5000 : Integer.parseInt(period);
    }

    private int getTestIdleInitialDelay() {
        String delay = props.getProperty("mail.idle.test.initialDelay");
        return Objects.isNull(delay) || delay.isEmpty() ? 0 : Integer.parseInt(delay);
    }

    private boolean idleSupported(Folder folder) {
        try {
            if (folder instanceof IMAPFolder) {
                IMAPFolder imapFolder = (IMAPFolder) folder;
                if (!folder.isOpen()) {
                    folder.open(Folder.READ_ONLY);
                }
                imapFolder.idle();
                return true;
            }
        } catch (MessagingException e) {
            log.error("the server doesn't support the IDLE extension");
        } catch (IllegalStateException e) {
            log.error(String.format("the %s isn't open", folder.getFullName()), e);
        }
        return false;
    }
}
