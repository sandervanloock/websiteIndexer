import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiEventManager} from 'ng-jhipster';

import {FeedItem} from './feed-item.model';
import {FeedItemPopupService} from './feed-item-popup.service';
import {FeedItemService} from './feed-item.service';

@Component( {
                selector: 'jhi-feed-item-delete-dialog', templateUrl: './feed-item-delete-dialog.component.html'
            } )
export class FeedItemDeleteDialogComponent {

    feedItem: FeedItem;

    constructor( private feedItemService: FeedItemService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager ) {
    }

    clear() {
        this.activeModal.dismiss( 'cancel' );
    }

    confirmDelete( id: number ) {
        this.feedItemService.delete( id ).subscribe( ( response ) => {
            this.eventManager.broadcast( {
                                             name: 'feedItemListModification', content: 'Deleted an feedItem'
                                         } );
            this.activeModal.dismiss( true );
        } );
    }
}

@Component( {
                selector: 'jhi-feed-item-delete-popup', template: ''
            } )
export class FeedItemDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor( private route: ActivatedRoute, private feedItemPopupService: FeedItemPopupService ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe( ( params ) => {
            this.feedItemPopupService
                .open( FeedItemDeleteDialogComponent as Component, params['id'] );
        } );
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
