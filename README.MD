Tag Manager
===========

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0) ![This script was last tested in Nuix 9.10](https://img.shields.io/badge/Script%20Tested%20in%20Nuix-9.10-green.svg)

View the GitHub project [here](https://github.com/Nuix/Tag-Manager) or download the latest release [here](https://github.com/Nuix/Tag-Manager/releases).

![image](https://user-images.githubusercontent.com/11775738/40191792-4d3b568a-59b7-11e8-83af-d871e9c46d26.png)

# Overview

**Written By:** Jason Wells

Provides a GUI to:
- Bulk remove tags from a case
- Move/copy tag structures

# Getting Started

## Setup

Begin by downloading the latest release of this code.  Extract the contents of the archive into your Nuix scripts directory.  In Windows the script directory is likely going to be either of the following:

- `%appdata%\Nuix\Scripts` - User level script directory
- `%programdata%\Nuix\Scripts` - System level script directory

## Usage

Begin by running the script in a Nuix case which has some tags.  This will display a dialog listing the tags in a tree structure.  Select one or more tags then select one of the available operations.  Operations available may vary depending on your selection of tags.

### Copy/Move Tags

Allows you to move or copy a nested tag to be nested under a different tag.

For example if I have a tag <code>Important&nbsp;Items|Cat&nbsp;Videos</code> and I decide that the tag <code>Cat&nbsp;videos</code> actually should be under a tag <code>Watch&nbsp;Later</code> rather than <code>Important&nbsp;Items</code>, I can move that tag to be nested beneath <code>Watch&nbsp;Later</code> resulting in a new tag <code>Watch&nbsp;Later|Cat&nbsp;Videos</code>.

Internally the script is generating a new tag name, applying that tag to the items with the old tag and then handling the old tag based on the setting of **Old Tag Handling**.

#### Old Tag Handling

When performing the copy/move operation on a tag, you provide a choice for **Old Tag Handling**:

| Choice | Description |
|--------|-------------|
| Remove Tag and Delete It | Once new tag is applied to items, the old tag is removed from those items and then deleted from the case. Basically a `move` operation. |
| Remove Tag | Once new tag is applied to items, the old tag is removed from those items, but not deleted from the case.  This leaves the old tag present but not applied to any items. |
| Copy Tag | Once new tag is applied to items, the old tag is left unchanged (not removed from items or case). |

### Delete Tags and Descendants

Removes selected tags and all descendant tags from all items in the case and then deletes those tags from the case.

### Filter on Selected Tag

Populates the text box governing which tags are currently visible with a regular expression to match the selected tag.

### Reload Tags from Case

The tag manager dialog will refresh the listing of tags when it performs operations, but it is still possible to add/remove tags in the case independent of the script while it is running.  There currently is no way for the script to be notified of these changes it did not make.  This button allows you to manually refresh the listing.

If you modify tags in the case outside of this script while the script is running and you want those changes to show up in the tag listing, click this button.

### Filter Visible

Allows you to provide a regular expression used to determine which tags are currently visible in the listing.  Note that in regular expression the pipe character (`|`) is a special character and therefore must be properly escaped when used in the filter by providing `\|`.

# License

```
Copyright 2022 Nuix

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
