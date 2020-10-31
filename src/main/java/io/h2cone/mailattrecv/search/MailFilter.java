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

import javax.mail.Message;
import javax.mail.search.SearchTerm;

import java.util.List;
import java.util.Objects;

/**
 * @author h
 */
public class MailFilter extends SearchTerm {
    private final List<SearchTerm> searchTerms;

    public MailFilter(MailQuery query) {
        this.searchTerms = query.searchTerms();
    }

    @Override
    public boolean match(Message msg) {
        for (SearchTerm term : searchTerms) {
            if (Objects.isNull(term) || !term.match(msg)) {
                return false;
            }
        }
        return true;
    }
}
