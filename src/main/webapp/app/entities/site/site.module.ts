import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {WebsiteIndexerSharedModule} from '../../shared';
import {
    SiteComponent,
    SiteDeleteDialogComponent,
    SiteDeletePopupComponent,
    SiteDetailComponent,
    SiteDialogComponent,
    SitePopupComponent,
    sitePopupRoute,
    SitePopupService,
    siteRoute,
    SiteService,
} from './';

const ENTITY_STATES = [
    ...siteRoute,
    ...sitePopupRoute,
];

@NgModule({
    imports: [
        WebsiteIndexerSharedModule,
        RouterModule.forRoot(ENTITY_STATES, {useHash: true})
    ],
    declarations: [
        SiteComponent,
        SiteDetailComponent,
        SiteDialogComponent,
        SiteDeleteDialogComponent,
        SitePopupComponent,
        SiteDeletePopupComponent,
    ],
    entryComponents: [
        SiteComponent,
        SiteDialogComponent,
        SitePopupComponent,
        SiteDeleteDialogComponent,
        SiteDeletePopupComponent,
    ],
    providers: [
        SiteService,
        SitePopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class WebsiteIndexerSiteModule {
}
