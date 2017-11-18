import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/Rx';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import {Site} from './site.model';
import {SiteService} from './site.service';
import {Principal, ResponseWrapper} from '../../shared';

@Component( {
                selector: 'jhi-site', templateUrl: './site.component.html'
            } )
export class SiteComponent implements OnInit, OnDestroy {
    sites: Site[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor( private siteService: SiteService, private alertService: JhiAlertService, private eventManager: JhiEventManager, private principal: Principal ) {
    }

    loadAll() {
        this.siteService.query().subscribe( ( res: ResponseWrapper ) => {
            this.sites = res.json;
        }, ( res: ResponseWrapper ) => this.onError( res.json ) );
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then( ( account ) => {
            this.currentAccount = account;
        } );
        this.registerChangeInSites();
    }

    ngOnDestroy() {
        this.eventManager.destroy( this.eventSubscriber );
    }

    trackId( index: number, item: Site ) {
        return item.id;
    }

    registerChangeInSites() {
        this.eventSubscriber = this.eventManager.subscribe( 'siteListModification', ( response ) => this.loadAll() );
    }

    private onError( error ) {
        this.alertService.error( error.message, null, null );
    }
}
