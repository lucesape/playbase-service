delete from playservice_images_playadvisor where location in (select id from playservice_locations_playadvisor where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_location_categories_playadvisor where location in (select id from playservice_locations_playadvisor where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_documents_playadvisor where location in (select id from playservice_locations_playadvisor where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_location_equipment_playadvisor where location in (select id from playservice_locations_playadvisor where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_location_facilities_playadvisor where location in (select id from playservice_locations_playadvisor where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_locations_playadvisor where project = 'TE_VERWIJDEREN_PROJECT';
delete from playservice_images where location in (select id from playservice_locations where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_location_categories where location in (select id from playservice_locations where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_documents where location in (select id from playservice_locations where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_location_equipment where location in (select id from playservice_locations where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_location_facilities where location in (select id from playservice_locations where project = 'TE_VERWIJDEREN_PROJECT');
delete from playservice_locations where project = 'TE_VERWIJDEREN_PROJECT';