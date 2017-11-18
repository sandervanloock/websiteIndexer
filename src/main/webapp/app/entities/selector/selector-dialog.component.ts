import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Response} from '@angular/http';

import {Observable} from 'rxjs/Rx';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import {Selector} from './selector.model';
import {SelectorPopupService} from './selector-popup.service';
import {SelectorService} from './selector.service';
import {Site, SiteService} from '../site';
import {ResponseWrapper} from '../../shared';

@Component( {
                selector: 'jhi-selector-dialog', templateUrl: './selector-dialog.component.html'
            } )
export class SelectorDialogComponent implements OnInit {

    selector: Selector;
    isSaving: boolean;

    sites: Site[];

    selectors: Selector[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private selectorService: SelectorService,
        private siteService: SiteService,
        private eventManager: JhiEventManager ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.siteService.query()
            .subscribe( ( res: ResponseWrapper ) => {
                this.sites = res.json;
            }, ( res: ResponseWrapper ) => this.onError( res.json ) );
        this.selectorService.query()
            .subscribe( ( res: ResponseWrapper ) => {
                this.selectors = res.json;
            }, ( res: ResponseWrapper ) => this.onError( res.json ) );
    }

    clear() {
        this.activeModal.dismiss( 'cancel' );
    }

    save() {
        this.isSaving = true;
        if ( this.selector.id !== undefined ) {
            this.subscribeToSaveResponse( this.selectorService.update( this.selector ) );
        } else {
            this.subscribeToSaveResponse( this.selectorService.create( this.selector ) );
        }
    }

    trackSiteById( index: number, item: Site ) {
        return item.id;
    }

    trackSelectorById( index: number, item: Selector ) {
        return item.id;
    }

    private subscribeToSaveResponse( result: Observable<Selector> ) {
        result.subscribe( ( res: Selector ) => this.onSaveSuccess( res ), ( res: Response ) => this.onSaveError( res ) );
    }

    private onSaveSuccess( result: Selector ) {
        this.eventManager.broadcast( {name: 'selectorListModification', content: 'OK'} );
        this.isSaving = false;
        this.activeModal.dismiss( result );
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

@Component( {
                selector: 'jhi-selector-popup', template: ''
            } )
export class SelectorPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor( private route: ActivatedRoute, private selectorPopupService: SelectorPopupService ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe( ( params ) => {
            if ( params['id'] ) {
                this.selectorPopupService
                    .open( SelectorDialogComponent as Component, params['id'] );
            } else {
                this.selectorPopupService
                    .open( SelectorDialogComponent as Component );
            }
        } );
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
