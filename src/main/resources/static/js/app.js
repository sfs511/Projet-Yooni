// ================================================================
//  SENEGAL TRIP PLANNER - app.js
// ================================================================

// --- TEST IMMEDIAT : verifie que app.js se charge ---
// Cette ligne s'execute en premier, avant tout le reste
(function() {
    var log = document.getElementById('tcpLog');
    if (log) {
        log.innerHTML = '';
        var d = document.createElement('div');
        d.className = 'tcp-line';
        d.textContent = '[BOOT] app.js charge avec succes.';
        log.appendChild(d);
    }
})();

// --- INITIALISATION CARTE ---
var map = L.map('map').setView([14.4974, -14.4524], 7);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

var regionsData     = [];
var routeLayerGroup = L.layerGroup().addTo(map);

// ================================================================
//  CONSOLE TCP
// ================================================================
function logTCP(message) {
    var log = document.getElementById('tcpLog');
    if (!log) return;
    var now   = new Date();
    var heure = String(now.getHours()).padStart(2, '0')   + ':'
              + String(now.getMinutes()).padStart(2, '0') + ':'
              + String(now.getSeconds()).padStart(2, '0');
    var div         = document.createElement('div');
    div.className   = 'tcp-line';
    div.textContent = '[' + heure + '] ' + message;
    log.appendChild(div);
    log.scrollTop = log.scrollHeight;
}

function clearLog() {
    var log = document.getElementById('tcpLog');
    if (log) log.innerHTML = '';
}

function setBadge(ok, texte) {
    var badge = document.getElementById('tcpStatus');
    if (!badge) return;
    badge.textContent = texte;
    badge.className   = ok ? 'network-badge online' : 'network-badge offline';
}

// ================================================================
//  PING TCP
// ================================================================
function verifierTCP() {
    setBadge(false, 'TCP : verification...');
    logTCP('Ping -> localhost:8080/api/socket/ping');
    fetch('http://localhost:8080/api/socket/ping')
        .then(function(r) { return r.text(); })
        .then(function(texte) {
            logTCP('Reponse : ' + texte);
            if (texte.indexOf('TCP OK') !== -1) {
                setBadge(true, 'TCP actif (port 9090)');
                logTCP('Serveur TCP operationnel !');
            } else {
                setBadge(false, 'TCP : inactif');
                logTCP('ATTENTION : ' + texte);
            }
        })
        .catch(function(err) {
            setBadge(false, 'TCP : erreur');
            logTCP('ERREUR : ' + err.message);
        });
}

// ================================================================
//  ENVOI COMMANDE TCP
// ================================================================
function envoyerCommandeTCP(commande) {
    logTCP('--- Commande : ' + commande + ' ---');
    logTCP('Connexion TCP port 9090...');
    return fetch('http://localhost:8080/api/socket/commande?cmd='
                 + encodeURIComponent(commande))
        .then(function(r) { return r.text(); })
        .then(function(texte) {
            texte.split('\n').forEach(function(ligne) {
                if (ligne.trim()) logTCP('  ' + ligne.trim());
            });
            logTCP('Connexion TCP fermee.');
            return texte;
        })
        .catch(function(err) {
            logTCP('ERREUR TCP : ' + err.message);
            throw err;
        });
}

// ================================================================
//  CHARGEMENT DES REGIONS
// ================================================================
function chargerRegions() {
    logTCP('Chargement regions via HTTP REST...');
    fetch('http://localhost:8080/api/trip/regions')
        .then(function(r) { return r.json(); })
        .then(function(data) {
            regionsData = data;
            logTCP('OK : ' + regionsData.length + ' regions chargees.');
            var selectDepart  = document.getElementById('startNode');
            var selectArrivee = document.getElementById('endNode');
            regionsData.sort(function(a, b) {
                if (a.id === 'DAKAR') return -1;
                if (b.id === 'DAKAR') return 1;
                return a.nom.localeCompare(b.nom);
            });
            regionsData.forEach(function(region) {
                var optDep         = document.createElement('option');
                optDep.value       = region.id;
                optDep.textContent = region.nom;
                if (region.id === 'DAKAR') optDep.selected = true;
                selectDepart.appendChild(optDep);
                var optArr         = document.createElement('option');
                optArr.value       = region.id;
                optArr.textContent = region.nom;
                selectArrivee.appendChild(optArr);
                var marker = L.marker([region.latitude, region.longitude]).addTo(map);
                marker.bindPopup(
                    '<b>Ville : ' + region.nom + '</b><br>' +
                    '<hr style="margin:4px 0">' +
                    '<span style="font-size:0.9em">' + region.description + '</span>'
                );
            });
        })
        .catch(function(err) {
            logTCP('ERREUR chargement : ' + err.message);
            afficherErreur('Impossible de contacter le serveur port 8080.');
        });
}

// ================================================================
//  CALCUL ITINERAIRE
// ================================================================
function calculerItineraire() {
    var departId   = document.getElementById('startNode').value;
    var arriveeId  = document.getElementById('endNode').value;
    var algorithme = document.getElementById('algorithmeSelect').value;
    var transport  = document.getElementById('transportSelect').value;
    if (!departId || !arriveeId) {
        alert('Veuillez selectionner un point de depart et une destination.');
        return;
    }
    if (departId === arriveeId) {
        alert('Le depart et la destination doivent etre differents.');
        return;
    }
    afficherChargement(true);
    routeLayerGroup.clearLayers();
    if (transport === 'tcp') {
        logTCP('=== MODE TCP SOCKET ===');
        var algoTCP = (algorithme === 'bellmanford') ? 'BELLMANFORD' : 'DIJKSTRA';
        envoyerCommandeTCP(algoTCP + ':' + departId + ':' + arriveeId)
            .then(function(reponse) {
                afficherReponseTCP(reponse);
                afficherChargement(false);
            })
            .catch(function() {
                afficherErreur('Erreur TCP. Cliquez sur Ping pour verifier.');
                afficherChargement(false);
            });
    } else {
        logTCP('=== MODE HTTP REST ===');
        var url = (algorithme === 'bellmanford')
            ? 'http://localhost:8080/api/trip/path/bellmanford?from=' + departId + '&to=' + arriveeId
            : 'http://localhost:8080/api/trip/path?from=' + departId + '&to=' + arriveeId;
        logTCP('GET ' + url);
        fetch(url)
            .then(function(r) { return r.json(); })
            .then(function(data) {
                logTCP('Reponse : ' + data.nombreEtapes + ' etapes, ' + data.distanceTotaleKm + ' km');
                var chemin = data.chemin;
                if (!chemin || chemin.length === 0) {
                    afficherErreur('Aucun itineraire trouve.');
                    afficherChargement(false);
                    return;
                }
                var latlngs    = chemin.map(function(e) { return [e.latitude, e.longitude]; });
                var nomsVilles = chemin.map(function(e) { return e.nom; });
                afficherDetailsChemin(data);
                ajouterMarqueursDebutFin(latlngs, nomsVilles[0], nomsVilles[nomsVilles.length - 1]);
                tracerRouteOSRM(latlngs, function() { afficherChargement(false); });
            })
            .catch(function(err) {
                logTCP('ERREUR HTTP : ' + err.message);
                afficherErreur('Erreur lors du calcul.');
                afficherChargement(false);
            });
    }
}

// ================================================================
//  CALCUL TSP
// ================================================================
function calculerTSP() {
    var transport = document.getElementById('transportSelect').value;
    afficherChargement(true);
    routeLayerGroup.clearLayers();
    if (transport === 'tcp') {
        logTCP('=== TSP via TCP SOCKET ===');
        envoyerCommandeTCP('TSP:DAKAR')
            .then(function(reponse) {
                afficherReponseTCP(reponse);
                afficherChargement(false);
            })
            .catch(function() {
                afficherErreur('Erreur TCP TSP.');
                afficherChargement(false);
            });
    } else {
        logTCP('=== TSP via HTTP REST ===');
        fetch('http://localhost:8080/api/trip/path/tsp?from=DAKAR')
            .then(function(r) { return r.json(); })
            .then(function(data) {
                logTCP('TSP : ' + data.nombreEtapes + ' etapes');
                var chemin = data.chemin;
                if (!chemin || chemin.length === 0) {
                    afficherErreur('Aucun itineraire TSP.');
                    afficherChargement(false);
                    return;
                }
                var latlngs    = chemin.map(function(e) { return [e.latitude, e.longitude]; });
                var nomsVilles = chemin.map(function(e) { return e.nom; });
                afficherDetailsChemin(data);
                ajouterMarqueursDebutFin(latlngs, nomsVilles[0], nomsVilles[nomsVilles.length - 1]);
                tracerRouteOSRM(latlngs, function() { afficherChargement(false); });
            })
            .catch(function() {
                afficherErreur('Erreur TSP HTTP.');
                afficherChargement(false);
            });
    }
}

// ================================================================
//  AFFICHAGE REPONSE TCP
// ================================================================
function afficherReponseTCP(reponse) {
    var outputDiv = document.getElementById('pathOutput');
    if (reponse.indexOf('ERREUR') !== -1) {
        outputDiv.innerHTML = '<span style="color:red;">' + reponse + '</span>';
        return;
    }
    var lignes = reponse.split('\n');
    var trajetNoms = [], distTxt = '', tempsTxt = '', etapesTxt = '', algoTxt = '';
    lignes.forEach(function(l) {
        var l2 = l.trim();
        if (l2.indexOf('Algorithme') !== -1) {
            var idx = l2.indexOf(':');
            if (idx !== -1) algoTxt = l2.substring(idx + 1).trim();
        }
        if (l2.indexOf('Trajet') !== -1) {
            var idx = l2.indexOf(':');
            if (idx !== -1) trajetNoms = l2.substring(idx + 1).trim().split(' -> ');
        }
        if (l2.indexOf('Distance') !== -1) distTxt   = l2.replace(/\|/g, '').trim();
        if (l2.indexOf('Temps')    !== -1) tempsTxt  = l2.replace(/\|/g, '').trim();
        if (l2.indexOf('Etapes')   !== -1) etapesTxt = l2.replace(/\|/g, '').trim();
    });
    if (trajetNoms.length > 1) {
        var latlngs = [];
        trajetNoms.forEach(function(nom) {
            for (var j = 0; j < regionsData.length; j++) {
                if (regionsData[j].nom === nom.trim()) {
                    latlngs.push([regionsData[j].latitude, regionsData[j].longitude]);
                    break;
                }
            }
        });
        if (latlngs.length > 1) {
            ajouterMarqueursDebutFin(latlngs, trajetNoms[0], trajetNoms[trajetNoms.length - 1]);
            tracerRouteOSRM(latlngs, null);
        }
    }
    var etapesHTML = '';
    trajetNoms.forEach(function(nom, i) {
        etapesHTML += '<div class="step"><span class="step-num">' + (i + 1)
                    + '</span><span>' + nom.trim() + '</span></div>';
        if (i < trajetNoms.length - 1) etapesHTML += '<div class="step-arrow">&#9660;</div>';
    });
    outputDiv.innerHTML =
        '<div class="result-stats">'
        + '<span>MODE : <b>TCP Socket</b></span>'
        + '<span>Algo : <b>' + algoTxt + '</b></span>'
        + '<span>' + distTxt + '</span>'
        + '<span>' + tempsTxt + '</span>'
        + '<span>' + etapesTxt + '</span>'
        + '</div>'
        + '<div class="route-steps">' + etapesHTML + '</div>';
}

// ================================================================
//  AFFICHAGE DETAILS CHEMIN (HTTP)
// ================================================================
function afficherDetailsChemin(data) {
    var outputDiv  = document.getElementById('pathOutput');
    var nomsVilles = data.chemin.map(function(e) { return e.nom; });
    var h = Math.floor(data.tempsEstimeMinutes / 60);
    var m = data.tempsEstimeMinutes % 60;
    var tf = (h > 0 && m > 0) ? h + 'h ' + m + 'min' : (h > 0) ? h + 'h' : m + ' min';
    var etapesHTML = '';
    for (var i = 0; i < nomsVilles.length; i++) {
        etapesHTML += '<div class="step"><span class="step-num">' + (i + 1)
                    + '</span><span>' + nomsVilles[i] + '</span></div>';
        if (i < nomsVilles.length - 1) etapesHTML += '<div class="step-arrow">&#9660;</div>';
    }
    outputDiv.innerHTML =
        '<div class="result-stats">'
        + '<span>MODE : <b>HTTP REST</b></span>'
        + '<span>Algo : <b>' + data.algorithme + '</b></span>'
        + '<span>Distance : <b>' + data.distanceTotaleKm + ' km</b></span>'
        + '<span>Temps : <b>' + tf + '</b></span>'
        + '<span>Etapes : <b>' + data.nombreEtapes + '</b></span>'
        + '</div>'
        + '<div class="route-steps">' + etapesHTML + '</div>';
}

// ================================================================
//  TRACE OSRM
// ================================================================
function tracerRouteOSRM(latlngs, callback) {
    var coords = latlngs.map(function(p) { return p[1] + ',' + p[0]; }).join(';');
    fetch('https://router.project-osrm.org/route/v1/driving/' + coords
          + '?overview=full&geometries=geojson')
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (data.routes && data.routes.length > 0) {
                var pts = data.routes[0].geometry.coordinates.map(function(c) {
                    return [c[1], c[0]];
                });
                var poly = L.polyline(pts, {
                    color: '#e74c3c', weight: 5, opacity: 0.85
                }).addTo(routeLayerGroup);
                map.fitBounds(poly.getBounds());
            } else {
                tracerLigneDroite(latlngs);
            }
            if (callback) callback();
        })
        .catch(function() {
            tracerLigneDroite(latlngs);
            if (callback) callback();
        });
}

function tracerLigneDroite(latlngs) {
    var poly = L.polyline(latlngs, {
        color: '#e74c3c', weight: 4, opacity: 0.7, dashArray: '8,6'
    }).addTo(routeLayerGroup);
    map.fitBounds(poly.getBounds());
}

// ================================================================
//  MARQUEURS
// ================================================================
function ajouterMarqueursDebutFin(latlngs, nomDepart, nomArrivee) {
    L.marker(latlngs[0], {
        icon: L.divIcon({
            className: '',
            html: '<div style="font-size:22px;line-height:1;">&#128994;</div>',
            iconSize: [24, 24], iconAnchor: [12, 12]
        })
    }).addTo(routeLayerGroup)
      .bindPopup('<b>Depart : ' + nomDepart + '</b>').openPopup();
    L.marker(latlngs[latlngs.length - 1], {
        icon: L.divIcon({
            className: '',
            html: '<div style="font-size:22px;line-height:1;">&#128308;</div>',
            iconSize: [24, 24], iconAnchor: [12, 12]
        })
    }).addTo(routeLayerGroup)
      .bindPopup('<b>Arrivee : ' + (nomArrivee || 'Fin') + '</b>');
}

// ================================================================
//  UTILITAIRES
// ================================================================
function afficherChargement(actif) {
    document.getElementById('planTripBtn').disabled = actif;
    document.getElementById('tspBtn').disabled      = actif;
    if (actif) {
        document.getElementById('pathOutput').innerHTML =
            '<span style="color:#888;">Calcul en cours...</span>';
    }
}

function afficherErreur(msg) {
    document.getElementById('pathOutput').innerHTML =
        '<span style="color:red;">Erreur : ' + msg + '</span>';
}

// ================================================================
//  EVENEMENTS
// ================================================================
document.getElementById('planTripBtn').addEventListener('click', calculerItineraire);
document.getElementById('tspBtn').addEventListener('click', calculerTSP);
document.getElementById('tcpPingBtn').addEventListener('click', function() {
    clearLog();
    verifierTCP();
});

// ================================================================
//  DEMARRAGE
// ================================================================
chargerRegions();
verifierTCP();
