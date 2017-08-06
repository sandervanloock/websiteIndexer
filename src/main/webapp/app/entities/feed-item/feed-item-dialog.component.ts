import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Response} from '@angular/http';

import {Observable} from 'rxjs/Rx';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import {FeedItem} from './feed-item.model';
import {FeedItemPopupService} from './feed-item-popup.service';
import {FeedItemService} from './feed-item.service';

@Component({
    selector: 'jhi-feed-item-dialog',
    templateUrl: './feed-item-dialog.component.html'
})
export class FeedItemDialogComponent implements OnInit {

    feedItem: FeedItem;
    isSaving: boolean;

    constructor(public activeModal: NgbActiveModal,
                private alertService: JhiAlertService,
                private feedItemService: FeedItemService,
                private eventManager: JhiEventManager) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.feedItem.id !== undefined) {
            this.subscribeToSaveResponse(
                this.feedItemService.update(this.feedItem));
        } else {
            this.subscribeToSaveResponse(
                this.feedItemService.create(this.feedItem));
        }
    }

    private subscribeToSaveResponse(result: Observable<FeedItem>) {
        result.subscribe((res: FeedItem) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: FeedItem) {
        this.eventManager.broadcast({name: 'feedItemListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-feed-item-popup',
    template: ''
})
export class FeedItemPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(private route: ActivatedRoute,
                private feedItemPopupService: FeedItemPopupService) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['id']) {
                this.feedItemPopupService
                    .open(FeedItemDialogComponent as Component, params['id']);
            } else {
                this.feedItemPopupService
                    .open(FeedItemDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
