import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Rx';
import {JhiEventManager} from 'ng-jhipster';

import {FeedItem} from './feed-item.model';
import {FeedItemService} from './feed-item.service';

@Component({
    selector: 'jhi-feed-item-detail',
    templateUrl: './feed-item-detail.component.html'
})
export class FeedItemDetailComponent implements OnInit, OnDestroy {

    feedItem: FeedItem;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(private eventManager: JhiEventManager,
                private feedItemService: FeedItemService,
                private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInFeedItems();
    }

    load(id) {
        this.feedItemService.find(id).subscribe((feedItem) => {
            this.feedItem = feedItem;
        });
    }

    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInFeedItems() {
        this.eventSubscriber = this.eventManager.subscribe(
            'feedItemListModification',
            (response) => this.load(this.feedItem.id)
        );
    }
}
