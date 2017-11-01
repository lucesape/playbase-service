/* 
 * Copyright (C) 2017 B3Partners B.V.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/* global url */

var matcher = null;
$(document).ready(function () {
    matcher = new CronTable();
    matcher.init();
});

function CronTable() {
    this.playadvisor = null;
    this.init = function () {
        this.playadvisor = $('#playbasetable').DataTable({
            /*"processing": true,
             "serverSide": true,*/
            "ajax": url + "tabledata",
            "columns": [
                {"data": "project"},
                {"data": "type_"},
                {"data": "cronexpressie"},
                {"data": "next_fire_time"},
            ]
        });
        var me = this;
        $('#playbasetable tbody').on('click', 'tr', (function () {
            if ($(this).hasClass('selected')) {
                $(this).removeClass('selected');
            } else {
                me.playadvisor.$('tr.selected').removeClass('selected');
                $(this).addClass('selected');
                me.playadvisorClicked(me.playadvisor.row(this).data());
            }
        })).bind(this);


        $('#button').click(function () {
            this.playbasetable.row('.selected').remove().draw(false);
        });

    };

    this.playadvisorClicked = function (data) {
        console.log("playadvisor clicked", data);
        window.location.href = url + "view?cronjobid=" + data.id;
    };

}