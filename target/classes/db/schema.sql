-- Schema SQL pour Yooni Delivery

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CLIENT', 'LIVREUR', 'ADMIN')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    ville VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS livreurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    vehicule VARCHAR(100),
    plaque_vehicule VARCHAR(20),
    zone VARCHAR(100),
    statut VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE' CHECK (statut IN ('DISPONIBLE', 'EN_LIVRAISON', 'HORS_SERVICE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS colis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    poids DOUBLE NOT NULL DEFAULT 0,
    dimensions VARCHAR(100),
    valeur_declaree DOUBLE DEFAULT 0,
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE', 'EN_COURS', 'LIVRE', 'ANNULE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS livraisons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    livreur_id BIGINT,
    colis_id BIGINT NOT NULL,
    adresse_depart VARCHAR(255) NOT NULL,
    adresse_arrivee VARCHAR(255) NOT NULL,
    ville_depart VARCHAR(100),
    ville_arrivee VARCHAR(100),
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE', 'PRISE_EN_CHARGE', 'EN_COURS', 'LIVREE', 'ANNULEE', 'ECHEC')),
    prix DOUBLE NOT NULL DEFAULT 0,
    distance_km DOUBLE DEFAULT 0,
    duree_estimee_minutes INT DEFAULT 0,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_prise_en_charge TIMESTAMP,
    date_livraison TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (livreur_id) REFERENCES livreurs(id) ON DELETE SET NULL,
    FOREIGN KEY (colis_id) REFERENCES colis(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS positions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    livreur_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    vitesse DOUBLE DEFAULT 0,
    direction DOUBLE DEFAULT 0,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (livreur_id) REFERENCES livreurs(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('LIVRAISON', 'LIVREUR', 'SYSTEME', 'PROMOTION')),
    lue BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index pour les performances
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_clients_user_id ON clients(user_id);
CREATE INDEX IF NOT EXISTS idx_livreurs_user_id ON livreurs(user_id);
CREATE INDEX IF NOT EXISTS idx_livreurs_statut ON livreurs(statut);
CREATE INDEX IF NOT EXISTS idx_livraisons_client_id ON livraisons(client_id);
CREATE INDEX IF NOT EXISTS idx_livraisons_livreur_id ON livraisons(livreur_id);
CREATE INDEX IF NOT EXISTS idx_livraisons_statut ON livraisons(statut);
CREATE INDEX IF NOT EXISTS idx_positions_livreur_id ON positions(livreur_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_lue ON notifications(user_id, lue);

-- Donnees de test
INSERT INTO users (email, password, telephone, role, actif) VALUES
('admin@yooni.sn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '770000001', 'ADMIN', TRUE),
('client@yooni.sn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '770000002', 'CLIENT', TRUE),
('livreur@yooni.sn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '770000003', 'LIVREUR', TRUE);

INSERT INTO clients (user_id, nom, prenom, adresse, ville) VALUES
(2, 'Diallo', 'Amadou', 'Rue 10, Plateau', 'Dakar');

INSERT INTO livreurs (user_id, nom, prenom, vehicule, plaque_vehicule, zone, statut) VALUES
(3, 'Ndiaye', 'Moussa', 'Moto', 'DK-123-A', 'Dakar', 'DISPONIBLE');