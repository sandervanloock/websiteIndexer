import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';

import {WebsiteIndexerAttributeModule} from './attribute/attribute.module';
import {WebsiteIndexerDocumentModule} from './document/document.module';
import {WebsiteIndexerFeedItemModule} from './feed-item/feed-item.module';
import {WebsiteIndexerSelectorModule} from './selector/selector.module';
import {WebsiteIndexerSiteModule} from './site/site.module';

/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        WebsiteIndexerAttributeModule,
        WebsiteIndexerDocumentModule,
        WebsiteIndexerFeedItemModule,
        WebsiteIndexerSelectorModule,
        WebsiteIndexerSiteModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class WebsiteIndexerEntityModule {}
