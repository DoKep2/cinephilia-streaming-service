import http from 'k6/http';
import {check, sleep} from 'k6';
import {randomItem} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js'

export const options = {
    vus: 5,
    duration: '10s',
};

const STREAMING_SERVICE_URL = 'https://itmo-sd.letsdeploy.space/api/v1/streaming/streaming';
const MOVIE_SERVICE_GET_RANDOM_MOVIE_ID = 'https://itmo-sd.letsdeploy.space/api/v1/movies?pageNumber=0&pageSize=10'

export function setup() {
    return getMovieId()
}

export function getMovieId() {
    return randomItem(http.get(MOVIE_SERVICE_GET_RANDOM_MOVIE_ID).json().map(movie => movie.movieId))
}

export default function (movieId) {

    const linksResponse = http.get(`${STREAMING_SERVICE_URL}/movie/links?movie_id=${movieId}`)
    check(linksResponse, {
        'is status OK ?': (r) => r.status === 200,
        'is status 404 ?': (r) => r.status === 404
    })
    sleep(1);
}