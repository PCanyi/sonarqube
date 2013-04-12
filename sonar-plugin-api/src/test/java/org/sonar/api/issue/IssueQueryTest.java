/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.issue;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class IssueQueryTest {

  @Test
  public void should_build_query() throws Exception {
    IssueQuery query = IssueQuery.builder()
      .keys(Lists.newArrayList("ABCDE"))
      .severities(Lists.newArrayList(Issue.SEVERITY_BLOCKER))
      .statuses(Lists.newArrayList(Issue.STATUS_RESOLVED))
      .assigneeLogins(Lists.newArrayList("gargantua"))
      .limit(125)
      .offset(33)
      .build();
    assertThat(query.keys()).containsExactly("ABCDE");
    assertThat(query.severities()).containsExactly(Issue.SEVERITY_BLOCKER);
    assertThat(query.statuses()).containsExactly(Issue.STATUS_RESOLVED);
    assertThat(query.assigneeLogins()).containsExactly("gargantua");
    assertThat(query.limit()).isEqualTo(125);
    assertThat(query.offset()).isEqualTo(33);
  }
}
