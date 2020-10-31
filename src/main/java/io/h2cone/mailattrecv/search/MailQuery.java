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

package io.h2cone.mailattrecv.search;

import javax.mail.search.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author h
 */
public class MailQuery {
    private String from;
    private String subject;

    public static MailQuery create(Properties props) {
        return new MailQuery()
                .setFrom(props.getProperty("mail.search.term.from"))
                .setSubject(props.getProperty("mail.search.term.subject"));
    }

    public List<SearchTerm> searchTerms() {
        return new ArrayList<SearchTerm>() {
            {
                if (Objects.nonNull(from) && !from.isEmpty()) {
                    add(new FromStringTerm(from));
                }
                if (Objects.nonNull(subject) && !subject.isEmpty()) {
                    add(new SubjectTerm(subject));
                }
            }
        };
    }

    public MailQuery setFrom(String from) {
        this.from = from;
        return this;
    }

    public MailQuery setSubject(String subject) {
        this.subject = subject;
        return this;
    }
}
