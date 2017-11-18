import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {WebsiteIndexerSharedModule} from '../../shared';
import {
    AttributeComponent,
    AttributeDeleteDialogComponent,
    AttributeDeletePopupComponent,
    AttributeDetailComponent,
    AttributeDialogComponent,
    AttributePopupComponent,
    attributePopupRoute,
    AttributePopupService,
    AttributeResolvePagingParams,
    attributeRoute,
    AttributeService,
} from './';

const ENTITY_STATES = [...attributeRoute, ...attributePopupRoute];

@NgModule( {
               imports: [WebsiteIndexerSharedModule, RouterModule.forRoot( ENTITY_STATES, {useHash: true} )],
               declarations: [AttributeComponent, AttributeDetailComponent, AttributeDialogComponent, AttributeDeleteDialogComponent, AttributePopupComponent,
                              AttributeDeletePopupComponent],
               entryComponents: [AttributeComponent, AttributeDialogComponent, AttributePopupComponent, AttributeDeleteDialogComponent, AttributeDeletePopupComponent],
               providers: [AttributeService, AttributePopupService, AttributeResolvePagingParams],
               schemas: [CUSTOM_ELEMENTS_SCHEMA]
           } )
export class WebsiteIndexerAttributeModule {
}
