import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Routes} from '@angular/router';

import {UserRouteAccessService} from '../../shared';
import {JhiPaginationUtil} from 'ng-jhipster';

import {FeedItemComponent} from './feed-item.component';
import {FeedItemDetailComponent} from './feed-item-detail.component';
import {FeedItemPopupComponent} from './feed-item-dialog.component';
import {FeedItemDeletePopupComponent} from './feed-item-delete-dialog.component';

@Injectable()
export class FeedItemResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
        };
    }
}

export const feedItemRoute: Routes = [
    {
        path: 'feed-item',
        component: FeedItemComponent,
        resolve: {
            'pagingParams': FeedItemResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'FeedItems'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'feed-item/:id',
        component: FeedItemDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'FeedItems'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const feedItemPopupRoute: Routes = [
    {
        path: 'feed-item-new',
        component: FeedItemPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'FeedItems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'feed-item/:id/edit',
        component: FeedItemPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'FeedItems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'feed-item/:id/delete',
        component: FeedItemDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'FeedItems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
