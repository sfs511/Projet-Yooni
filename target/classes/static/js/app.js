document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    initMap();
    loadRegions();
    setupAlgorithmSelector();
});

let map;
let markers = [];
let routeLayer;

function initMap() {
    map = L.map('map').setView([14.7167, -17.4677], 7);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);
}

async function loadRegions() {
    try {
        const res = await fetch('/api/trip/regions');
        const regions = await res.json();
        regions.forEach(region => {
            const marker = L.marker([region.latitude, region.longitude])
                .addTo(map)
                .bindPopup(`<b>${region.nom}</b><br>${region.description}`);
            markers.push({ id: region.id, marker });
        });
    } catch (e) {
        console.error('Erreur chargement regions:', e);
    }
}

function setupAlgorithmSelector() {
    const form = document.getElementById('tripForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const from = document.getElementById('depart').value;
            const to = document.getElementById('arrivee').value;
            const algo = document.getElementById('algorithme').value;

            let url = '/api/trip/path';
            if (algo === 'bellmanford') url = '/api/trip/path/bellmanford';
            if (algo === 'tsp') url = '/api/trip/path/tsp?from=' + from;

            if (algo !== 'tsp') url += '?from=' + from + '&to=' + to;

            try {
                const res = await fetch(url);
                const data = await res.json();
                displayRoute(data);
            } catch (e) {
                console.error('Erreur calcul itineraire:', e);
            }
        });
    }
}

function displayRoute(data) {
    if (routeLayer) map.removeLayer(routeLayer);

    const coords = data.chemin.map(r => [r.latitude, r.longitude]);
    routeLayer = L.polyline(coords, { color: '#3388ff', weight: 3 }).addTo(map);

    markers.forEach(m => map.removeLayer(m.marker));
    markers = [];

    data.chemin.forEach((region, i) => {
        const marker = L.marker([region.latitude, region.longitude])
            .addTo(map)
            .bindPopup(`<b>${i + 1}. ${region.nom}</b>`)
            .openPopup();
        markers.push({ id: region.id, marker });
    });

    const info = document.getElementById('routeInfo');
    if (info) {
        info.innerHTML = `
            <h3>${data.algorithme}</h3>
            <p>Distance: ${data.distanceTotaleKm} km</p>
            <p>Temps estime: ${data.tempsEstimeMinutes} min</p>
            <p>Etapes: ${data.nombreEtapes}</p>
        `;
    }

    map.fitBounds(routeLayer.getBounds());
}