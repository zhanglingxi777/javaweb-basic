import require from "../utils/require";

export function login(username, password, verifyCode, uuid) {
    const data = {
        username, password, verifyCode, uuid
    };
    return require({
        url: "/8/login",
        headers: {
            isToken: false
        },
        method: 'post',
        data: data
    })
}

export function getVerifyImage() {
    return require({
        url: '/8/login',
        headers: {
            isToken: false
        },
        method: 'get',
        timeout: 20000
    })
}