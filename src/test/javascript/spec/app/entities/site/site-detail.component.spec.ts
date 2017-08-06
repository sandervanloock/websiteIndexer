/* tslint:disable max-line-length */
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {OnInit} from '@angular/core';
import {DatePipe} from '@angular/common';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Rx';
import {JhiDataUtils, JhiDateUtils, JhiEventManager} from 'ng-jhipster';
import {WebsiteIndexerTestModule} from '../../../test.module';
import {MockActivatedRoute} from '../../../helpers/mock-route.service';
import {SiteDetailComponent} from '../../../../../../main/webapp/app/entities/site/site-detail.component';
import {SiteService} from '../../../../../../main/webapp/app/entities/site/site.service';
import {Site} from '../../../../../../main/webapp/app/entities/site/site.model';

describe('Component Tests', () => {

    describe('Site Management Detail Component', () => {
        let comp: SiteDetailComponent;
        let fixture: ComponentFixture<SiteDetailComponent>;
        let service: SiteService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [WebsiteIndexerTestModule],
                declarations: [SiteDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    SiteService,
                    JhiEventManager
                ]
            }).overrideTemplate(SiteDetailComponent, '')
                .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SiteDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SiteService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new Site(10)));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.site).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
