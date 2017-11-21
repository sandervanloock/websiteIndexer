import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TaskOverviewComponent} from './crawlers/task-overview.component';
import {RouterModule} from '@angular/router';
import {UserRouteAccessService} from '../shared/auth/user-route-access-service';
import {TasksService} from './crawlers/tasks.service';
import {TasksTrackingService} from './crawlers/tasks-tracking.service';
import {CrawlerDetailComponent} from './crawlers/crawler-detail/crawler-detail.component';
import {CrawlerCreateComponent} from './crawlers/crawler-create/crawler-create.component';
import {WebsiteIndexerSharedModule} from '../shared';

const ENTITY_STATES = [{
    path: 'tasks', component: TaskOverviewComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Tasks'
    }, canActivate: [UserRouteAccessService], // outlet: 'popup'
}, {
    path: 'tasks/crawlers/new', component: CrawlerCreateComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Create new Crawl'
    }
}, {
    path: 'tasks/crawlers/:id', component: CrawlerDetailComponent, data: {
        authorities: ['ROLE_USER'], pageTitle: 'Crawler'
    }
}];

@NgModule( {
               imports: [CommonModule, RouterModule.forRoot( ENTITY_STATES, {useHash: true} ), WebsiteIndexerSharedModule],
               declarations: [TaskOverviewComponent, CrawlerDetailComponent, CrawlerCreateComponent],
               providers: [TasksService, TasksTrackingService]
           } )
export class TasksModule {
}
