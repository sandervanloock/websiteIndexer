import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Rx';
import {JhiEventManager} from 'ng-jhipster';

import {Selector} from './selector.model';
import {SelectorService} from './selector.service';

@Component( {
                selector: 'jhi-selector-detail', templateUrl: './selector-detail.component.html'
            } )
export class SelectorDetailComponent implements OnInit, OnDestroy {

    selector: Selector;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor( private eventManager: JhiEventManager, private selectorService: SelectorService, private route: ActivatedRoute ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe( ( params ) => {
            this.load( params['id'] );
        } );
        this.registerChangeInSelectors();
    }

    load( id ) {
        this.selectorService.find( id ).subscribe( ( selector ) => {
            this.selector = selector;
        } );
    }

    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy( this.eventSubscriber );
    }

    registerChangeInSelectors() {
        this.eventSubscriber = this.eventManager.subscribe( 'selectorListModification', ( response ) => this.load( this.selector.id ) );
    }
}
