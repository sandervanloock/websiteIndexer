import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/Rx';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import {Selector} from './selector.model';
import {SelectorService} from './selector.service';
import {Principal, ResponseWrapper} from '../../shared';

@Component( {
                selector: 'jhi-selector', templateUrl: './selector.component.html'
            } )
export class SelectorComponent implements OnInit, OnDestroy {
    selectors: Selector[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor( private selectorService: SelectorService, private alertService: JhiAlertService, private eventManager: JhiEventManager, private principal: Principal ) {
    }

    loadAll() {
        this.selectorService.query().subscribe( ( res: ResponseWrapper ) => {
            this.selectors = res.json;
        }, ( res: ResponseWrapper ) => this.onError( res.json ) );
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then( ( account ) => {
            this.currentAccount = account;
        } );
        this.registerChangeInSelectors();
    }

    ngOnDestroy() {
        this.eventManager.destroy( this.eventSubscriber );
    }

    trackId( index: number, item: Selector ) {
        return item.id;
    }

    registerChangeInSelectors() {
        this.eventSubscriber = this.eventManager.subscribe( 'selectorListModification', ( response ) => this.loadAll() );
    }

    private onError( error ) {
        this.alertService.error( error.message, null, null );
    }
}
