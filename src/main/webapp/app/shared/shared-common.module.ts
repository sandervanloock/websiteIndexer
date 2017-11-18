import {LOCALE_ID, NgModule} from '@angular/core';
import {Title} from '@angular/platform-browser';

import {WindowRef} from './tracker/window.service';
import {JhiAlertComponent, JhiAlertErrorComponent, WebsiteIndexerSharedLibsModule} from './';

@NgModule( {
               imports: [WebsiteIndexerSharedLibsModule], declarations: [JhiAlertComponent, JhiAlertErrorComponent], providers: [WindowRef, Title, {
        provide: LOCALE_ID, useValue: 'en'
    }], exports: [WebsiteIndexerSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
           } )
export class WebsiteIndexerSharedCommonModule {
}
