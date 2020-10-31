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

package io.h2cone.mailattrecv.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author h
 */
public class AttachmentDownloader {
    private static final Logger log = LoggerFactory.getLogger(AttachmentDownloader.class);

    static final String CONTENT_TYPE = "multipart";
    private final Folder folder;
    private final String saveDir;

    public AttachmentDownloader(Folder folder, Properties props) {
        this.saveDir = getSaveDir(props);
        this.folder = folder;
    }

    public List<String> download(int msgNum) throws MessagingException, IOException {
        List<String> files = new ArrayList<>();
        Message message = folder.getMessage(msgNum);
        String contentType = message.getContentType();
        if (contentType.contains(CONTENT_TYPE)) {
            Multipart multiPart = (Multipart) message.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    String file = genFile(part);
                    part.saveFile(file);
                    files.add(file);
                }
            }
        }
        return files;
    }

    private String getSaveDir(Properties props) {
        String saveDir = props.getProperty("mail.download.attachment.saveDir");
        return Objects.isNull(saveDir) || saveDir.isEmpty() ? System.getProperty("user.dir") + File.separator + "attachments" : saveDir;
    }

    private String genFile(MimeBodyPart part) throws MessagingException {
        String fileName = part.getFileName();
        try {
            fileName = MimeUtility.decodeText(fileName);
        } catch (UnsupportedEncodingException e) {
            log.error("failed to decode text: " + fileName, e);
        }
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            String shortName = fileName.substring(0, index);
            String extension = fileName.substring(index);
            fileName = shortName + genSuffix() + extension;
        } else {
            fileName = fileName + genSuffix();
        }
        return saveDir + File.separator + fileName;
    }

    private String genSuffix() {
        return "-" + System.currentTimeMillis();
    }
}
