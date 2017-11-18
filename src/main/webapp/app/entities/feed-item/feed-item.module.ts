import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {WebsiteIndexerSharedModule} from '../../shared';
import {
    FeedItemComponent,
    FeedItemDeleteDialogComponent,
    FeedItemDeletePopupComponent,
    FeedItemDetailComponent,
    FeedItemDialogComponent,
    FeedItemPopupComponent,
    feedItemPopupRoute,
    FeedItemPopupService,
    FeedItemResolvePagingParams,
    feedItemRoute,
    FeedItemService,
} from './';

const ENTITY_STATES = [...feedItemRoute, ...feedItemPopupRoute];

@NgModule( {
               imports: [WebsiteIndexerSharedModule, RouterModule.forRoot( ENTITY_STATES, {useHash: true} )],
               declarations: [FeedItemComponent, FeedItemDetailComponent, FeedItemDialogComponent, FeedItemDeleteDialogComponent, FeedItemPopupComponent,
                              FeedItemDeletePopupComponent],
               entryComponents: [FeedItemComponent, FeedItemDialogComponent, FeedItemPopupComponent, FeedItemDeleteDialogComponent, FeedItemDeletePopupComponent],
               providers: [FeedItemService, FeedItemPopupService, FeedItemResolvePagingParams],
               schemas: [CUSTOM_ELEMENTS_SCHEMA]
           } )
export class WebsiteIndexerFeedItemModule {
}
