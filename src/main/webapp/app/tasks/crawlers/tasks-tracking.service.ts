import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Observer} from 'rxjs/Observer';
import {AuthServerProvider} from '../../shared/index';
import {WindowRef} from '../../shared/tracker/window.service';

import * as SockJS from 'sockjs-client';
import * as Stomp from 'webstomp-client';

@Injectable()
export class TasksTrackingService {
    stompClient = null;
    subscriber = null;
    connection: Promise<any>;
    connectedPromise: any;
    listener: Observable<any>;
    listenerObserver: Observer<any>;
    alreadyConnectedOnce = false;

    constructor( private authServerProvider: AuthServerProvider, private $window: WindowRef ) {

    }

    connect() {
        if ( !this.connectedPromise ) {
            this.connection = this.createConnection();
        }
        // building absolute path so that websocket doesn't fail when deploying with a context path
        const loc = this.$window.nativeWindow.location;
        let url;
        url = '//' + loc.host + loc.pathname + 'websocket/crawlstat/1';
        const authToken = this.authServerProvider.getToken();
        if ( authToken ) {
            url += '?access_token=' + authToken;
        }
        const socket = new SockJS( url );
        this.stompClient = Stomp.over( socket );
        const headers = {};
        this.stompClient.connect( headers, () => {
            this.connectedPromise( 'success' );
            this.connectedPromise = null;
            // subscribe here
            this.stompClient.subscribe( '/topic/crawlstat', ( message ) => console.log( message ) );
        } );
    }

    disconnect() {
        if ( this.stompClient !== null ) {
            this.stompClient.disconnect();
            this.stompClient = null;
        }
        this.alreadyConnectedOnce = false;
    }

    private createConnection(): Promise<any> {
        return new Promise( ( resolve, error ) => this.connectedPromise = resolve );
    }
}
