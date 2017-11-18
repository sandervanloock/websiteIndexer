import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {DatePipe} from '@angular/common';

import {
    AccountService,
    AuthServerProvider,
    CSRFService,
    HasAnyAuthorityDirective,
    JhiLoginModalComponent,
    JhiTrackerService,
    LoginModalService,
    LoginService,
    Principal,
    StateStorageService,
    UserService,
    WebsiteIndexerSharedCommonModule,
    WebsiteIndexerSharedLibsModule
} from './';

@NgModule( {
               imports: [WebsiteIndexerSharedLibsModule, WebsiteIndexerSharedCommonModule],
               declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
               providers: [LoginService, LoginModalService, AccountService, StateStorageService, Principal, CSRFService, JhiTrackerService, AuthServerProvider, UserService,
                           DatePipe],
               entryComponents: [JhiLoginModalComponent],
               exports: [WebsiteIndexerSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective, DatePipe],
               schemas: [CUSTOM_ELEMENTS_SCHEMA]

           } )
export class WebsiteIndexerSharedModule {
}
