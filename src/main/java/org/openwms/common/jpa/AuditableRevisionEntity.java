/*
 * Copyright 2005-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.jpa;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * A AuditableRevisionEntity is mapped onto Hibernate Envers Revision table and extended about the current user.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "COM_SRV_REVISION")
@RevisionEntity(AuditableEntityListener.class)
class AuditableRevisionEntity implements Serializable {

    @Id
    @GeneratedValue
    @RevisionNumber
    @Column(name = "C_PK")
    private Long pk;

    @Column(name = "C_TIMESTAMP")
    @RevisionTimestamp
    private long timestamp;

    @Column(name = "C_USER")
    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }
}