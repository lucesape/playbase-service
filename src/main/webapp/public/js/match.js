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

var matcher = null;
$(document).ready(function () {
    matcher = new PlaymappingMatcher()
    matcher.init();
});

function PlaymappingMatcher() {
    this.playadvisor = null;
    this.playmappingtable = null;
    this.init = function () {
        this.playadvisor = $('#playbasetable').DataTable({
            /*"processing": true,
             "serverSide": true,*/
            "ajax": url + "dataPlayadvisor",
            "columns": [
                {"data": "title"},
                {"data": "pa_id"}
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
        this.loadPlaymappingTable(data.id);
        $("#playadvisor").text(data.title + " (" + data.id + ")");
        $("#playadvisorId").val(data.id);
    };

    this.loadPlaymappingTable = function (id) {
        if (this.playmappingtable) {
            this.playmappingtable.destroy();
        }
        this.playmappingtable = $('#playmappingtable').DataTable({
            "ajax": {
                url: url + "dataPlaymapping",
                data: {
                    playadvisorId: id
                }
            },
            "columns": [
                {"data": "title"},
               // {"data": "pm_guid"},
                {"data": "score"},
                {"data": "distance"},
                {"data": "similarity"}
            ],
            order: [[1, "desc"],[2, "asc"],[2, "asc"]]
        });
        var me = this;
        $('#playmappingtable tbody').on('click', 'tr', (function () {
            if ($(this).hasClass('selected')) {
                $(this).removeClass('selected');
            } else {
                me.playmappingtable.$('tr.selected').removeClass('selected');
                $(this).addClass('selected');
                me.playmappingClicked(me.playmappingtable.row(this).data());
            }
        })).bind(this);


        $('#button').click(function () {
            this.playmappingtable.row('.selected').remove().draw(false);
        });
    };
    
    this.playmappingClicked = function(data){
      
        $("#playmapping").text(data.title + " (" + data.id + ")");
        $("#playmappingId").val(data.id);  
    };
}