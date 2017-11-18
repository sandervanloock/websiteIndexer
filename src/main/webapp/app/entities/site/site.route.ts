import {Routes} from '@angular/router';

import {UserRouteAccessService} from '../../shared';

import {SiteComponent} from './site.component';
import {SiteDetailComponent} from './site-detail.component';
import {SitePopupComponent} from './site-dialog.component';
import {SiteDeletePopupComponent} from './site-delete-dialog.component';

export const siteRoute: Routes = [{
    path: 'site', component: SiteComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Sites'
    }, canActivate: [UserRouteAccessService]
}, {
    path: 'site/:id', component: SiteDetailComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Sites'
    }, canActivate: [UserRouteAccessService]
}];

export const sitePopupRoute: Routes = [{
    path: 'site-new', component: SitePopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Sites'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}, {
    path: 'site/:id/edit', component: SitePopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Sites'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}, {
    path: 'site/:id/delete', component: SiteDeletePopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Sites'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}];
