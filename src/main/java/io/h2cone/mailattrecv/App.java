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
import io.h2cone.mailattrecv.common.PropertiesUtils;
import io.h2cone.mailattrecv.download.AttachmentDownloader;
import io.h2cone.mailattrecv.listen.AttachmentReceiver;
import io.h2cone.mailattrecv.search.MailFilter;
import io.h2cone.mailattrecv.search.MailQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import javax.mail.*;
import java.util.Properties;
import java.util.concurrent.*;

import static picocli.CommandLine.*;

/**
 * @author h
 */
@Command(name = "mail-attrecv", description = "Download email attachments automatically.", mixinStandardHelpOptions = true, version = "1.0")
public class App implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    @Option(names = "-c", defaultValue = "app.properties", description = "Path to properties file.")
    private String propsPath;

    public static void main(String[] args) {
        App app = new App();
        CommandLine cli = new CommandLine(app);
        int code = cli.execute(args);
        if (code == 0) {
            log.info("prepare to receive message, propsPath: {}", app.propsPath);
        }
    }

    @Override
    public Integer call() throws Exception {
        Properties props = PropertiesUtils.loadProps(propsPath);
        ImapClient client = new ImapClient(props);
        Folder inbox = client.getInbox();
        if (!inbox.isOpen()) {
            inbox.open(Folder.READ_ONLY);
        }
        MailQuery query = MailQuery.create(props);
        MailFilter filter = new MailFilter(query);
        AttachmentDownloader downloader = new AttachmentDownloader(inbox, props);
        inbox.addMessageCountListener(new AttachmentReceiver(filter, downloader));
        client.testIdle(inbox);
        return 0;
    }
}
