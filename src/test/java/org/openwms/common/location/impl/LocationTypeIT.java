/*
 * Copyright 2018 Heiko Scherrer
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
package org.openwms.common.location.impl;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openwms.common.location.LocationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A LocationTypeIT.
 *
 * @author Heiko Scherrer
 */
@ExtendWith(SpringExtension.class)// RunWith(SpringRunner.class)
@Tag("IntegrationTest")
@DataJpaTest
class LocationTypeIT {

    @Autowired
    private LocationTypeRepository repository;

    @Test
    void testUniqueConstraint() {
        repository.saveAndFlush(new LocationType("conveyor"));
        assertThatThrownBy(
                () -> repository.saveAndFlush(new LocationType("conveyor")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
