//페이지가 로드될 때 서버에서 토큰을 설정합니다.
document.addEventListener('DOMContentLoaded', function() {
    //서버에서 발급받은 토큰을 페이지에서 동적으로 설정합니다.
    const token = document.getElementById('token').textContent;

    function pollingToken() {
        fetch(`/token?token=${token}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => response.json())
            .then(data => {
                document.getElementById('tokenInfo').innerHTML = `
                        <p>Token: ${data.token}</p>
                        <p>Rank: ${data.rank}</p>
                        <p>TTL: ${data.ttl}</p>
                    `;
            })
            .catch(error => console.error('Error fetching token info:', error));
    }

    //5초마다 폴링 요청을 보냅니다.
    setInterval(pollingToken, 5000);

    //페이지 로드 시 한 번 호출
    pollingToken();
});