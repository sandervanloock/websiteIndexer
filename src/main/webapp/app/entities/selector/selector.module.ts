import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {WebsiteIndexerSharedModule} from '../../shared';
import {
    SelectorComponent,
    SelectorDeleteDialogComponent,
    SelectorDeletePopupComponent,
    SelectorDetailComponent,
    SelectorDialogComponent,
    SelectorPopupComponent,
    selectorPopupRoute,
    SelectorPopupService,
    selectorRoute,
    SelectorService,
} from './';

const ENTITY_STATES = [...selectorRoute, ...selectorPopupRoute];

@NgModule( {
               imports: [WebsiteIndexerSharedModule, RouterModule.forRoot( ENTITY_STATES, {useHash: true} )],
               declarations: [SelectorComponent, SelectorDetailComponent, SelectorDialogComponent, SelectorDeleteDialogComponent, SelectorPopupComponent,
                              SelectorDeletePopupComponent],
               entryComponents: [SelectorComponent, SelectorDialogComponent, SelectorPopupComponent, SelectorDeleteDialogComponent, SelectorDeletePopupComponent],
               providers: [SelectorService, SelectorPopupService],
               schemas: [CUSTOM_ELEMENTS_SCHEMA]
           } )
export class WebsiteIndexerSelectorModule {
}
