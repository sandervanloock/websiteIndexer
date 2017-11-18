import {Routes} from '@angular/router';

import {UserRouteAccessService} from '../../shared';

import {SelectorComponent} from './selector.component';
import {SelectorDetailComponent} from './selector-detail.component';
import {SelectorPopupComponent} from './selector-dialog.component';
import {SelectorDeletePopupComponent} from './selector-delete-dialog.component';

export const selectorRoute: Routes = [{
    path: 'selector', component: SelectorComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Selectors'
    }, canActivate: [UserRouteAccessService]
}, {
    path: 'selector/:id', component: SelectorDetailComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Selectors'
    }, canActivate: [UserRouteAccessService]
}];

export const selectorPopupRoute: Routes = [{
    path: 'selector-new', component: SelectorPopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Selectors'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}, {
    path: 'selector/:id/edit', component: SelectorPopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Selectors'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}, {
    path: 'selector/:id/delete', component: SelectorDeletePopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Selectors'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}];
