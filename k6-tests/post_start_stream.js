import http from 'k6/http';
import {check, sleep} from 'k6';
import {randomItem} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js'

export const options = {
    vus: 5,
    duration: '10s',
};

const STREAMING_SERVICE_URL = 'https://itmo-sd.letsdeploy.space/api/v1/streaming/streaming';
const MOVIE_SERVICE_GET_RANDOM_MOVIE_ID = 'https://itmo-sd.letsdeploy.space/api/v1/movies?pageNumber=0&pageSize=10'
const USER_SERVICE_GET_RANDOM_USER_ID = 'https://itmo-sd.letsdeploy.space/api/v1/users?pageSize=10'

export function setup() {
    return getMovieId(), getUserId()
}

export function getMovieId() {
    return randomItem(http.get(MOVIE_SERVICE_GET_RANDOM_MOVIE_ID).json().map(movie => movie.movieId))
}

export function getUserId() {
    return randomItem(http.get(USER_SERVICE_GET_RANDOM_USER_ID).json().map(user => user.userId))
}

export default function (movieId, userId) {
    console.log(movieId)
    const startStreamResponse = http.post(`${STREAMING_SERVICE_URL}/actions/start?user_id=${userId}movie_id=${movieId}`)
    check(startStreamResponse, {
        'is status OK ?': (r) => r.status === 200,
        'is mode play ?': (r) => {
            return r.json().mode === "Play";
        },
    })
    sleep(1);
}