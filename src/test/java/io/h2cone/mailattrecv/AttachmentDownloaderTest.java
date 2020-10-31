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

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AttachmentDownloaderTest extends Tester {

    @Test
    public void download() throws IOException, MessagingException {
        int msgNum = 121;
        AttachmentDownloader downloader = new AttachmentDownloader(openInbox(), loadProps());
        List<String> paths = downloader.download(msgNum);
        Assert.assertNotNull(paths);
        for (String path : paths) {
            File file = new File(path);
            System.out.printf("path: %s, exited: %b", file.getPath(), file.exists());
        }
    }
}
