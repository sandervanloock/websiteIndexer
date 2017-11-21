import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TaskOverviewComponent} from './crawlers/task-overview.component';
import {RouterModule} from '@angular/router';
import {UserRouteAccessService} from '../shared/auth/user-route-access-service';
import {TasksService} from './crawlers/tasks.service';
import {TasksTrackingService} from './crawlers/tasks-tracking.service';

@NgModule( {
               imports: [CommonModule, RouterModule.forRoot( [{
                   path: 'tasks', component: TaskOverviewComponent, data: {
                       authorities: ['ROLE_USER'], pageTitle: 'Tasks'
                   }, canActivate: [UserRouteAccessService], // outlet: 'popup'
               }], {useHash: true} )], declarations: [TaskOverviewComponent], providers: [TasksService, TasksTrackingService]
           } )
export class TasksModule {
}
