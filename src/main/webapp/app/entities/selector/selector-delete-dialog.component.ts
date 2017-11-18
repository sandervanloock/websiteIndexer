import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';

import {Selector} from './selector.model';
import {SelectorPopupService} from './selector-popup.service';
import {SelectorService} from './selector.service';

@Component( {
                selector: 'jhi-selector-delete-dialog', templateUrl: './selector-delete-dialog.component.html'
            } )
export class SelectorDeleteDialogComponent {

    selector: Selector;

    constructor( private selectorService: SelectorService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager ) {
    }

    clear() {
        this.activeModal.dismiss( 'cancel' );
    }

    confirmDelete( id: number ) {
        this.selectorService.delete( id ).subscribe( ( response ) => {
            this.eventManager.broadcast( {
                                             name: 'selectorListModification', content: 'Deleted an selector'
                                         } );
            this.activeModal.dismiss( true );
        } );
    }
}

@Component( {
                selector: 'jhi-selector-delete-popup', template: ''
            } )
export class SelectorDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor( private route: ActivatedRoute, private selectorPopupService: SelectorPopupService ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe( ( params ) => {
            this.selectorPopupService
                .open( SelectorDeleteDialogComponent as Component, params['id'] );
        } );
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
