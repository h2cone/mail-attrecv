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

import io.h2cone.mailattrecv.download.AttachmentDownloader;
import org.junit.Assert;
import org.junit.Test;

import javax.mail.Folder;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttachmentDownloaderTest extends Tester {
    private final String msgNum = System.getenv("mail_msg_num");

    @Test
    public void download() throws IOException, MessagingException {
        Folder inbox = openInbox();
        AttachmentDownloader downloader = new AttachmentDownloader(inbox, loadProps());
        List<String> paths;
        if (Objects.isNull(msgNum) || msgNum.isEmpty()) {
            paths = downloader.download(inbox.getMessageCount());
        } else {
            paths = downloader.download(Integer.parseInt(msgNum));
        }
        Assert.assertNotNull(paths);
        List<File> files = new ArrayList<>();
        for (String path : paths) {
            File file = new File(path);
            System.out.printf("path: %s, exited: %b\n", file.getPath(), file.exists());
            files.add(file);
        }
        files.forEach(File::deleteOnExit);
        System.out.println("delete all files after passing the test, number of files: " + files.size());
    }
}
