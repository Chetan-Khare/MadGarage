-- V43: Fix incorrect logo URLs for Maruti Suzuki and Force
UPDATE makes SET logo_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Maruti_Suzuki_logo.svg/512px-Maruti_Suzuki_logo.svg.png' WHERE name = 'Maruti Suzuki';
UPDATE makes SET logo_url = 'https://upload.wikimedia.org/wikipedia/en/thumb/0/07/Force_Motors_logo.svg/512px-Force_Motors_logo.svg.png' WHERE name = 'Force';
UPDATE makes SET logo_url = 'https://upload.wikimedia.org/wikipedia/en/thumb/0/07/Force_Motors_logo.svg/512px-Force_Motors_logo.svg.png' WHERE name = 'Force Motors';
