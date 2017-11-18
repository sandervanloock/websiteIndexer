import {Component, OnInit} from '@angular/core';
import {TasksService} from './tasks.service';
import {ResponseWrapper} from '../../shared/model/response-wrapper.model';
import {SiteService} from '../../entities/site/site.service';
import {Site} from '../../entities/site/site.model';

@Component( {
                selector: 'jhi-task-overview', templateUrl: './task-overview.component.html', styles: []
            } )
export class TaskOverviewComponent implements OnInit {
    sites: Site[];

    constructor( private taskService: TasksService, private siteService: SiteService ) {
    }

    ngOnInit() {
        this.siteService.query( {
                                    page: 0, size: 10
                                } ).subscribe( ( res: ResponseWrapper ) => this.sites = res.json );
    }

}
