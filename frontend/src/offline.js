import {openDB} from 'idb';
const dbPromise=openDB('reliefgrid-outbox',1,{upgrade(db){db.createObjectStore('outbox',{keyPath:'key'});db.createObjectStore('conflicts',{keyPath:'key'});}});
export async function queueRequest(request){const item={key:crypto.randomUUID(),request,queuedAt:new Date().toISOString()};await (await dbPromise).put('outbox',item);return item;}
export async function pending(){return (await dbPromise).getAll('outbox');}
export async function conflicts(){return (await dbPromise).getAll('conflicts');}
export async function syncPending(){const db=await dbPromise,items=await pending();if(!items.length)return {results:[],count:0};const response=await fetch('/api/sync',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(items.map(({key,request})=>({key,request})))});if(!response.ok)throw new Error('Sync HTTP '+response.status);const results=await response.json();for(const result of results){if(['CREATED','REPLAY'].includes(result.status))await db.delete('outbox',result.key);else if(['CONFLICT','INVALID'].includes(result.status)){const item=items.find(x=>x.key===result.key);await db.put('conflicts',{...item,...result});await db.delete('outbox',result.key);}}return {results,count:results.length};}
export async function discardConflict(key){await (await dbPromise).delete('conflicts',key);}
