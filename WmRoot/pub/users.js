/*
 * Copyright ? 1996 - 2017 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors. 
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG. 
*/
//*******************************
//
// Code for Users Page
//



var groups = new Array();
var Dirty = 0;
var previousUser;
var addingUser = false;
var Str_All_Users;
var selectedGroup = ""; 


function quickSave(oTo, newValue)
{

    if (oTo == memberusersList)
    groups[groupList.options[groupList.selectedIndex].value][newValue] = newValue;

    
    if (oTo == availableusersList)
    groups[groupList.options[groupList.selectedIndex].value][newValue] = "";
    

    if (oTo == membergroupsList)
    groups[newValue][userList.options[userList.selectedIndex].value] = userList.options[userList.selectedIndex].value;

    if (oTo == availablegroupsList)
    groups[newValue][userList.options[userList.selectedIndex].value] = "";

    if (newValue == userList.options[userList.selectedIndex].value)
    loadUser(userList.options[userList.selectedIndex].value);
    
    if (newValue == groupList.options[groupList.selectedIndex].value)
    loadGroup(groupList.options[groupList.selectedIndex].value);

    
}

function moveitem (oFrom, oTo)  
{
    if(selectedGroup == "Everybody")
    {
        alert("The Everybody group cannot be modified.");
    }
    else
    {
        Dirty = 1;

        if (oTo != null && oFrom != null)
        {
            moveItemsDeny(oFrom,oTo, true);
        }
    }
}


function addGroup(group, users)
{

    //Arrays must be "prepped" to be containainers of arrays.
    groups[group] = ["placeholder"]
    groups[group].length = 0
    
    users.sort();
    
    for (user in users)
    {
        groups[group][users[user]] = users[user];
    }

}




function populateAvailableGroupsList()
{

    clearList(availablegroupsList);

    for (group in groups)
    {
        insertOptionABC(group,group, availablegroupsList, false);
    }
}

function populateGroupList()
{

    clearList(groupList);

    for (group in groups)
    {
        insertOptionABC(group,group, groupList, false);
    }
}


function populateAvailableUsersList()
{

    clearList(availableusersList);
    for (user in groups["Everybody"])
    {
        insertOption(user,user, availableusersList, false)
    }


}


function populateUserList(start, end)
{
    start = start.toLowerCase();
    end = end.toLowerCase();

//  if (end == "") 
//  {
//      end = "z";
//  }

    clearList(userList);
    for (user in groups["Everybody"])
    {
//      if (user.charAt(0).toLowerCase() >= start && user.charAt(0).toLowerCase() <= end)
//      {
            insertOption(user,user, userList, false)
//      }
    }
// filtering is disabled now due to i18n issues - CMARCELLIN

}


function filterUserList(filter)
{

    filter = filter.toLowerCase();
    var start = "";
    var end = "";


    //G   case (one letter)
    if (filter.length == 1)
    {
        start = filter;
        end = filter;
        populateUserList(start, end);
        return false;
    }

    //All case (string)
    if (filter == Str_All_Users)
    {
        start = "";
        end   = "";
        populateUserList(start, end);
        return false;
    }

    //#AB case (number)
    if (filter.charAt(0) == "#")
    {
        start = "";
        end   = filter.charAt(filter.length-1);
        populateUserList(start, end);
        return false;
    }

    //CDE case (multiple letters)
    //M-P case (same as above)
    start = filter.charAt(0);
    end   = filter.charAt(filter.length-1);
    populateUserList(start, end);

    userList.selectedIndex = 0;
    loadUser(userList.options[userList.selectedIndex].value);
    
    return false;

}



function loadUser(user) 
{

    previousUser = user;


    clearList(availablegroupsList);
    clearList(membergroupsList);
    populateAvailableGroupsList();


    for (group in groups)
    {

        if (groups[group][user] == user)
        {
            insertOptionABC(group,group, membergroupsList, false);
            removeOption(group, availablegroupsList);
        }

    }
    
    addNone(availablegroupsList);
    addNone(membergroupsList);

}


function changeSelectedGroup(group)
{
    if(group == "---none--")
    {
    }
    else
    {
        selectOption(groupList, group);
        loadGroup(group);
    }
}

function changeSelectedUser(user)
{
    if(user == "---none--")
    {
    }
    else
    {
        selectOption(userList, user);
        loadUser(user);
    }
}


function loadGroup(group)   
{
    if(group == "---none--")
    {
    }
    else
    {
        selectedGroup = group;

        //clearList(availableusersList);
        //clearList(memberusersList);
        
        if (group == "Everybody")
        {

            clearList(memberusersList);
            for (user in groups["Everybody"])
            {
                insertOption(user,user, memberusersList, false)
            }

            clearList(availableusersList);


        }
        else
        {
            //populateAvailableUsersList();
            
        
            for (var j = availableusersList.options.length; j > 0; j--) {
                if ( availableusersList.options[j-1].value == "---none--" )
                {
                    var nonevalue = availableusersList.options[j-1].value;
                    removeOption(nonevalue,availableusersList );
                }
            }

            conditionalmoveOptions(memberusersList, availableusersList, null ,groups[group]);       
            conditionalmoveOptions(availableusersList, memberusersList, groups[group], null);


            //if (groups[group][user] == user)

        }
        
        
        addNone(availableusersList);
        
        
        addNone(memberusersList);
    }
}



function changeUser(user)
{
    if(user == "---none--")
    {
    }
    else
    {
        loadUser(user);
    }
}

function changeGroup(group)
{
    if(group == "---none--")
    {
    }
    else
    {
        loadGroup(group);
    }
}


function insertUser()
{
    newUserName = newUser.value;

    if (newUserName == "")
    {
        alert("User Name requires a name.");
        newUser.focus();
        return false;
    }

    var invalidItems = /\;|\:|\,|\s|\_|\<|\>|\@|\?|\#|\*|\&|\^|\~|\%|\!|\$/ig;
    if (newUserName.match(invalidItems))
    {
        alert("Invalid name for a User.  User Names can only contain letters and numbers.");
        newUser.focus();
        return false;
    }

    for (var i=0; i< userList.length; i++)
    {
        if (userList.options[i].value.toLowerCase() == newUsername.toLowerCase())
        {
            alert("User name is already being used.");
            userList.selectedIndex = i;
            changeUser(userList.options[i].value);
            newUser.focus();
            return false;
        }
    }

    insertOptionABC(newUsername,newUsername, userList, true);

    clearList(membergroupsList);
    addNone(membergroupsList);

    clearList(availablegroupsList);
    populateAvailableGroupsList();

    newUser.value = "";

}

function saveChanges()
{

    var saveData = "";


    for (var i=0; i < groupList.length; i++)
    {
        group = groupList.options[i].value;
        saveData += "" + group + ";";

        var userData = "";

        for (user in groups["Everybody"])
        {
            if (groups[group][user] == user)
                userData += "" + user + ",";
        }
        saveData += userData + ":";
    }
    
    hiddenSave.value = saveData;
    hiddenAction.value = "update";

}


function submitForm()
{
    if (addingUser)
    {
        insertUser();
        return false;
    }
    Dirty = 0;
    return true;
}


function checkDirty () {

    if (Dirty > 0)
    {
        event.returnValue = '--- User and Group changes have not been saved. ---';
    }
}



function removeUser()
{

    if (userList.length > 0)
    {   
        UsertoRemove = userList.options[userList.selectedIndex].value
        if (confirm("Are you sure you want to remove " + UsertoRemove + "?"))
        {
            userList.options[userList.selectedIndex] = null;
            if (userList.length > 0)
            {
                userList.selectedIndex = 0;
                changeUser(userList.options[0].value);
            }
            else
            {
                changeUser("");
            }
            
            //FIX!!!!  Doesn't delete in memory yet
            //for (var i=0; i < availablegroupsList.length; i++)
            //{
            //ACLs[ACLtoRemove][availablegroupsList.options[i].value] = ""
            //}     
        }
    }
}


function buildFilterList(filterList)
{
    clearList(userfilterList);

    for (filterIndex in filterList)
    {
        var filter = filterList[filterIndex];
        insertOption(filter,filter, userfilterList, false);
    }

}

function editPassword(userName)
{
    var string = "You have not saved changes.  Continue without saving?"

    if (Dirty > 0)
    {
        if (!confirm(string)) return false;
    }
    if(is_csrf_guard_enabled && needToInsertToken) {
		createForm("htmlform_user_edit", "user-edit.dsp", "POST", "BODY");
		setFormProperty("htmlform_user_edit", "username", userName);
		setFormProperty("htmlform_user_edit", _csrfTokenNm_, _csrfTokenVal_);
        document.htmlform_user_edit.submit();
    } else {
		window.location.href='user-edit.dsp?username=' + encodeURIComponent(userName);
    }
    return false;

}

function setupPage()
{
    
    Str_All_Users = "All Users";
    //buildFilterList([Str_All_Users, "ABC","DEF", "GHI", "JKL", "MNO", "PQR", "STU", "VWX", "YZ"]);

    
    populateUserList("", "");
    populateAvailableGroupsList();

    //userfilterList.selectedIndex = 0;
    userList.selectedIndex = 0;
    loadUser(userList.options[userList.selectedIndex].value);


    populateGroupList();
    populateAvailableUsersList();
    
    groupList.selectedIndex = 0;
    loadGroup(groupList.options[groupList.selectedIndex].value);
    
    
}

