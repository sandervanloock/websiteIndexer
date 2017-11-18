import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Routes} from '@angular/router';

import {UserRouteAccessService} from '../../shared';
import {JhiPaginationUtil} from 'ng-jhipster';

import {AttributeComponent} from './attribute.component';
import {AttributeDetailComponent} from './attribute-detail.component';
import {AttributePopupComponent} from './attribute-dialog.component';
import {AttributeDeletePopupComponent} from './attribute-delete-dialog.component';

@Injectable()
export class AttributeResolvePagingParams implements Resolve<any> {

    constructor( private paginationUtil: JhiPaginationUtil ) {
    }

    resolve( route: ActivatedRouteSnapshot, state: RouterStateSnapshot ) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage( page ), predicate: this.paginationUtil.parsePredicate( sort ), ascending: this.paginationUtil.parseAscending( sort )
        };
    }
}

export const attributeRoute: Routes = [{
    path: 'attribute', component: AttributeComponent, resolve: {
        'pagingParams': AttributeResolvePagingParams
    }, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Attributes'
    }, canActivate: [UserRouteAccessService]
}, {
    path: 'attribute/:id', component: AttributeDetailComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Attributes'
    }, canActivate: [UserRouteAccessService]
}];

export const attributePopupRoute: Routes = [{
    path: 'attribute-new', component: AttributePopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Attributes'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}, {
    path: 'attribute/:id/edit', component: AttributePopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Attributes'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}, {
    path: 'attribute/:id/delete', component: AttributeDeletePopupComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Attributes'
    }, canActivate: [UserRouteAccessService], outlet: 'popup'
}];
