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
package org.openwms.common.account;

import org.ameba.exception.NotFoundException;
import org.ameba.http.MeasuredRestController;
import org.ameba.i18n.Translator;
import org.openwms.common.account.api.AccountVO;
import org.openwms.common.account.impl.AccountMapper;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;

import static org.openwms.common.CommonMessageCodes.ACCOUNT_NOT_FOUND;
import static org.openwms.common.CommonMessageCodes.ACCOUNT_NO_DEFAULT;
import static org.openwms.common.account.api.AccountApiConstants.API_ACCOUNTS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A AccountController is a measured and Spring managed REST controller that serves Account information.
 *
 * @author Heiko Scherrer
 */
@MeasuredRestController
public class AccountController extends AbstractWebController {

    private final AccountService service;
    private final AccountMapper mapper;
    private final Translator translator;

    public AccountController(AccountService service, AccountMapper mapper, Translator translator) {
        this.service = service;
        this.mapper = mapper;
        this.translator = translator;
    }

    @GetMapping(API_ACCOUNTS)
    public ResponseEntity<List<AccountVO>> findAll() {
        return ResponseEntity.ok(mapper.convertToVO(service.findAll()));
    }

    @GetMapping(value = API_ACCOUNTS, params = "default")
    public ResponseEntity<AccountVO> findDefault() {
        return ResponseEntity.ok(
                mapper.convertToVO(
                        service.findDefault().orElseThrow(
                                ()-> new NotFoundException(translator, ACCOUNT_NO_DEFAULT))
                ));
    }

    @GetMapping(value = API_ACCOUNTS, params = "identifier")
    public ResponseEntity<AccountVO> findByIdentifier(@RequestParam("identifier") String identifier) {
        return ResponseEntity.ok(
                mapper.convertToVO(
                        service.findByIdentifier(identifier).orElseThrow(
                                () -> new NotFoundException(translator, ACCOUNT_NOT_FOUND,
                                        new Serializable[]{identifier}, identifier))
                ));
    }

    @GetMapping(value = API_ACCOUNTS, params = "name")
    public ResponseEntity<AccountVO> findByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(
                mapper.convertToVO(
                        service.findByName(name).orElseThrow(
                                ()-> new NotFoundException(translator, ACCOUNT_NOT_FOUND,
                                        new Serializable[]{name}, name))
                ));
    }

    @GetMapping(API_ACCOUNTS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(AccountController.class).findAll()).withRel("accounts-findall"),
                        linkTo(methodOn(AccountController.class).findDefault()).withRel("accounts-finddefault"),
                        linkTo(methodOn(AccountController.class).findByIdentifier("identifier")).withRel("accounts-findbyidentifier"),
                        linkTo(methodOn(AccountController.class).findByName("name")).withRel("accounts-findbyname")
                )
        );
    }
}
