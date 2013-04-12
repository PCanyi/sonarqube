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
package org.sonar.batch.issue;

import org.sonar.api.database.model.Snapshot;
import org.sonar.api.issue.Issue;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.batch.index.ScanPersister;
import org.sonar.batch.index.SnapshotCache;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.IssueDao;
import org.sonar.core.issue.IssueDto;

import static com.google.common.collect.Lists.newArrayList;

public class IssuePersister implements ScanPersister {

  private final IssueDao dao;
  private final IssueCache cache;
  private final SnapshotCache snapshotCache;
  private final RuleFinder ruleFinder;

  public IssuePersister(IssueDao dao, IssueCache cache, SnapshotCache snapshotCache, RuleFinder ruleFinder) {
    this.dao = dao;
    this.cache = cache;
    this.snapshotCache = snapshotCache;
    this.ruleFinder = ruleFinder;
  }

  @Override
  public void persist() {
    for (Issue issue : cache.issues()) {
      Snapshot snapshot = snapshotCache.get(issue.componentKey());
      if (snapshot == null) {
        throw new IllegalStateException("Snapshot should not be null");
      }
      Rule rule = ruleFinder.findByKey(issue.ruleRepositoryKey(), issue.ruleKey());
      if (rule == null) {
        throw new IllegalStateException("Rule should not be null");
      }

      IssueDto issueDto = toIssueDto((DefaultIssue) issue, snapshot.getResourceId(), rule.getId());
      if (issue.isNew()) {
        dao.insert(issueDto);
      } else {
        // TODO do a batch update to get modified
        dao.update(newArrayList(issueDto));
      }
    }
  }

  private IssueDto toIssueDto(DefaultIssue issue, Integer componentId, Integer ruleId) {
    return new IssueDto()
        .setUuid(issue.key())
        .setLine(issue.line())
        .setTitle(issue.title())
        .setMessage(issue.message())
        .setCost(issue.cost())
        .setResolution(issue.resolution())
        .setStatus(issue.status())
        .setChecksum(issue.getChecksum())
        .setManualIssue(issue.isManual())
        .setManualSeverity(issue.isManualSeverity())
        .setUserLogin(issue.userLogin())
        .setAssigneeLogin(issue.assigneeLogin())
        .setCreatedAt(issue.createdAt())
        .setUpdatedAt(issue.updatedAt())
        .setClosedAt(issue.closedAt())
        .setRuleId(ruleId)
        .setResourceId(componentId)

            // TODO
        .setData(null)
//        .setPersonId()
        ;
  }
}
