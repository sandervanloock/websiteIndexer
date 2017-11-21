import {Component, OnInit} from '@angular/core';
import {SiteService} from '../../../entities/site/site.service';
import {Site} from '../../../entities/site/site.model';
import {ResponseWrapper} from '../../../shared';
import {JhiAlertService} from 'ng-jhipster';
import {Crawl} from '../crawl.model';
import {TasksService} from '../tasks.service';
import {Observable} from 'rxjs/Observable';
import {CrawlStats} from '../crawl-stats.model';

@Component( {
                selector: 'jhi-crawler-create', templateUrl: './crawler-create.component.html', styles: []
            } )
export class CrawlerCreateComponent implements OnInit {

    crawl: Crawl = new Crawl();
    isSaving: boolean;

    sites: Site[];

    constructor( private siteService: SiteService, private alertService: JhiAlertService, private tasksService: TasksService ) {
    }

    ngOnInit() {
        this.siteService.query()
            .subscribe( ( res: ResponseWrapper ) => {
                this.sites = res.json;
            }, ( res: ResponseWrapper ) => this.onError( res.json ) );
    }

    save() {
        this.isSaving = true;
        this.subscribeToSaveResponse( this.tasksService.startCrawl( this.crawl.site ) );
    }

    private subscribeToSaveResponse( result: Observable<CrawlStats> ) {
        result.subscribe( ( res: CrawlStats ) => this.onSaveSuccess( res ), ( res: Response ) => this.onSaveError( res ) );
    }

    private onSaveSuccess( result: CrawlStats ) {
        //this.eventManager.broadcast( {name: 'attributeListModification', content: 'OK'} );
        this.isSaving = false;
        //this.activeModal.dismiss( result );
    }

    private onSaveError( error ) {
        try {
            error.json();
        } catch ( exception ) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError( error );
    }

    private onError( error ) {
        this.alertService.error( error.message, null, null );
    }
}
