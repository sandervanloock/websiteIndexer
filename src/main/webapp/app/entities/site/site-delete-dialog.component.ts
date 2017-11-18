import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';

import {Site} from './site.model';
import {SitePopupService} from './site-popup.service';
import {SiteService} from './site.service';

@Component( {
                selector: 'jhi-site-delete-dialog', templateUrl: './site-delete-dialog.component.html'
            } )
export class SiteDeleteDialogComponent {

    site: Site;

    constructor( private siteService: SiteService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager ) {
    }

    clear() {
        this.activeModal.dismiss( 'cancel' );
    }

    confirmDelete( id: number ) {
        this.siteService.delete( id ).subscribe( ( response ) => {
            this.eventManager.broadcast( {
                                             name: 'siteListModification', content: 'Deleted an site'
                                         } );
            this.activeModal.dismiss( true );
        } );
    }
}

@Component( {
                selector: 'jhi-site-delete-popup', template: ''
            } )
export class SiteDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor( private route: ActivatedRoute, private sitePopupService: SitePopupService ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe( ( params ) => {
            this.sitePopupService
                .open( SiteDeleteDialogComponent as Component, params['id'] );
        } );
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
