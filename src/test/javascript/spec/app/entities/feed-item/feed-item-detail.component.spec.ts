/* tslint:disable max-line-length */
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {OnInit} from '@angular/core';
import {DatePipe} from '@angular/common';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Rx';
import {JhiDataUtils, JhiDateUtils, JhiEventManager} from 'ng-jhipster';
import {WebsiteIndexerTestModule} from '../../../test.module';
import {MockActivatedRoute} from '../../../helpers/mock-route.service';
import {FeedItemDetailComponent} from '../../../../../../main/webapp/app/entities/feed-item/feed-item-detail.component';
import {FeedItemService} from '../../../../../../main/webapp/app/entities/feed-item/feed-item.service';
import {FeedItem} from '../../../../../../main/webapp/app/entities/feed-item/feed-item.model';

describe('Component Tests', () => {

    describe('FeedItem Management Detail Component', () => {
        let comp: FeedItemDetailComponent;
        let fixture: ComponentFixture<FeedItemDetailComponent>;
        let service: FeedItemService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [WebsiteIndexerTestModule],
                declarations: [FeedItemDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    FeedItemService,
                    JhiEventManager
                ]
            }).overrideTemplate(FeedItemDetailComponent, '')
                .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(FeedItemDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(FeedItemService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new FeedItem(10)));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.feedItem).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
