import './vendor.ts';

import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {Ng2Webstorage} from 'ng2-webstorage';

import {UserRouteAccessService, WebsiteIndexerSharedModule} from './shared';
import {WebsiteIndexerHomeModule} from './home/home.module';
import {WebsiteIndexerAdminModule} from './admin/admin.module';
import {WebsiteIndexerAccountModule} from './account/account.module';
import {WebsiteIndexerEntityModule} from './entities/entity.module';
import {TasksModule} from './tasks/tasks.module';

import {customHttpProvider} from './blocks/interceptor/http.provider';
import {PaginationConfig} from './blocks/config/uib-pagination.config';
import {ErrorComponent, FooterComponent, JhiMainComponent, LayoutRoutingModule, NavbarComponent, PageRibbonComponent, ProfileService} from './layouts';

// jhipster-needle-angular-add-module-import JHipster will add new module here

@NgModule( {
               imports: [BrowserModule, LayoutRoutingModule, Ng2Webstorage.forRoot( {prefix: 'jhi', separator: '-'} ), WebsiteIndexerSharedModule, WebsiteIndexerHomeModule,
                         WebsiteIndexerAdminModule, WebsiteIndexerAccountModule, WebsiteIndexerEntityModule, // jhipster-needle-angular-add-module JHipster will add new module here
                         TasksModule],
               declarations: [JhiMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
               providers: [ProfileService, customHttpProvider(), PaginationConfig, UserRouteAccessService],
               bootstrap: [JhiMainComponent]
           } )
export class WebsiteIndexerAppModule {
}
