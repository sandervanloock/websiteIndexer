import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TaskOverviewComponent} from './task-overview/task-overview.component';
import {RouterModule} from '@angular/router';
import {UserRouteAccessService} from '../shared/auth/user-route-access-service';
import {TasksService} from './task-overview/tasks.service';

@NgModule( {
               imports: [CommonModule, RouterModule.forRoot( [{
                   path: 'tasks', component: TaskOverviewComponent, data: {
                       authorities: ['ROLE_USER'], pageTitle: 'Tasks'
                   }, canActivate: [UserRouteAccessService], // outlet: 'popup'
               }], {useHash: true} )], declarations: [TaskOverviewComponent], providers: [TasksService]
           } )
export class TasksModule
{
}
